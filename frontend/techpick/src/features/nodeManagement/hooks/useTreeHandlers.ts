import { NodeData } from '@/shared/types';
import { createNode } from '@/features/nodeManagement/utils/createNode';
import { moveNode } from '@/features/nodeManagement/utils/moveNode';
import { useGetRootAndRecycleBinData } from '@/features/nodeManagement/api/useGetRootAndRecycleBinData';
import { useTreeStore } from '@/shared/stores/treeStore';
import { CreateHandler, MoveHandler, NodeApi } from 'react-arborist';
import { useMoveFolder } from '@/features/nodeManagement/api/useMoveFolder';
import { useGetDefaultFolderData } from '@/features/nodeManagement/api/useGetDefaultFolderData';
import { useRenameFolder } from '@/features/nodeManagement/api/useRenameFolder';
import { removeByIdFromStructure } from '@/features/nodeManagement/utils/removeByIdFromStructure';
import { getNewIdFromStructure } from '@/features/nodeManagement/utils/getNewIdFromStructure';
import { useQueryClient } from '@tanstack/react-query';
import { ApiStructureData } from '@/shared/types/ApiTypes';
import toast from 'react-hot-toast';
import { deleteFolder } from '@/features/nodeManagement/api/queryFunctions';
import { getCurrentTreeTypeByNode } from '@/features/nodeManagement/utils/getCurrentTreeTypeByNode';

export const useTreeHandlers = () => {
  const { data: structureData, refetch: refetchStructure } =
    useGetRootAndRecycleBinData();
  const {
    treeRef,
    focusedNode,
    focusedFolderNodeList,
    focusedLinkNodeList,
    setFocusedNode,
    setFocusedLinkNodeList,
    setFocusedFolderNodeList,
  } = useTreeStore();
  const { data: defaultFolderIdData } = useGetDefaultFolderData();
  const { mutateAsync: moveFolder } = useMoveFolder();
  const { mutateAsync: renameFolder } = useRenameFolder();
  const queryClient = useQueryClient();
  const recycleBinId = defaultFolderIdData?.RECYCLE_BIN;

  const handleCreate: CreateHandler<NodeData> = async ({
    parentId,
    parentNode,
    index,
    type,
  }): Promise<{ id: string }> => {
    const newLocalNodeId = getNewIdFromStructure(
      structuredClone(structureData!)
    );

    // 폴더 생성 (클라이언트)
    const updatedTreeData = createNode(
      structuredClone(structureData!),
      type,
      parentId,
      parentNode,
      index,
      -1, // 가상 id, 서버에서 생성된 id로 대체됨
      'New Folder' // 가상 이름, 입력받은 이름으로 대체됨
    );

    queryClient.setQueryData(
      ['rootAndRecycleBinData'],
      (oldData: ApiStructureData) => ({
        root: updatedTreeData,
        recycleBin: oldData.recycleBin,
      })
    );

    return { id: newLocalNodeId.toString() };
  };

  const handleDrag: MoveHandler<NodeData> = async ({
    dragIds,
    dragNodes,
    parentId,
    parentNode,
    index,
  }) => {
    console.log('dragIds', dragIds);
    console.log('dragNodes', dragNodes);
    console.log('parentId', parentId);
    console.log('parentNode', parentNode);
    console.log('index', index);
    const isRoot = getCurrentTreeTypeByNode(dragNodes[0], treeRef) === 'root';
    const currentStructureData = isRoot
      ? structuredClone(structureData!.root)
      : structuredClone(structureData!.recycleBin);
    const currentRootId = isRoot
      ? defaultFolderIdData!.ROOT
      : defaultFolderIdData!.RECYCLE_BIN;

    const updatedTreeData = moveNode(
      currentStructureData,
      focusedNode,
      focusedFolderNodeList,
      focusedLinkNodeList,
      setFocusedFolderNodeList,
      setFocusedLinkNodeList,
      dragIds[0],
      dragNodes[0],
      parentId,
      parentNode,
      index
    );
    // 서버에 업데이트된 트리 전송
    const serverData = {
      parentFolderId: parentNode ? parentNode.data.folderId : currentRootId,
      structure: {
        root: isRoot ? updatedTreeData : structureData!.root,
        recycleBin: isRoot ? structureData!.recycleBin : updatedTreeData,
      },
    };

    await moveFolder({
      folderId: dragNodes[0].data.folderId!.toString(),
      structure: serverData,
    });
    await refetchStructure();
  };

  const handleRename = async ({
    name,
    node,
  }: {
    id: string;
    name: string;
    node: NodeApi;
  }) => {
    const realNodeId =
      node.data.type === 'folder' ? node.data.folderId : node.data.pickId;
    try {
      await renameFolder({ folderId: realNodeId.toString(), name });
      await refetchStructure();
    } catch (error) {
      console.error('Folder rename failed:', error);
      toast.error('동일한 이름을 가진 폴더가 존재합니다.');
      treeRef.rootRef.current?.root?.reset();
      await refetchStructure();
    }
  };

  const handleMoveToTrash = async ({
    ids,
    nodes,
  }: {
    ids: string[];
    nodes: NodeApi[];
  }) => {
    const realNodeId =
      nodes[0].data.type === 'folder'
        ? nodes[0].data.folderId
        : nodes[0].data.pickId;

    const updatedRecycleBin = structuredClone(structureData!.recycleBin);
    updatedRecycleBin.splice(0, 0, nodes[0].data);
    console.log('LOCAL : updatedRecycleBin', updatedRecycleBin);

    const updatedTreeStructure = removeByIdFromStructure(
      structuredClone(structureData!.root),
      ids[0]
    );

    const serverData = {
      parentFolderId: recycleBinId,
      structure: {
        root: updatedTreeStructure,
        recycleBin: updatedRecycleBin,
      },
    };
    await moveFolder({
      folderId: realNodeId.toString(),
      structure: serverData,
    });
    await refetchStructure();
    setFocusedNode(null);
  };

  const handleDelete = async ({
    ids,
    nodes,
  }: {
    ids: string[];
    nodes: NodeApi[];
  }) => {
    const realNodeId =
      nodes[0].data.type === 'folder'
        ? nodes[0].data.folderId
        : nodes[0].data.pickId;

    const updatedRecycleBin = removeByIdFromStructure(
      structuredClone(structureData!.recycleBin),
      ids[0]
    );

    const serverData = {
      parentFolderId: recycleBinId,
      structure: {
        root: structuredClone(structureData!.root),
        recycleBin: updatedRecycleBin,
      },
    };
    await deleteFolder({
      folderId: realNodeId.toString(),
      structure: serverData,
    });
    await refetchStructure();

    setFocusedNode(null);
  };

  return {
    handleCreate,
    handleDrag,
    handleRename,
    handleMoveToTrash,
    handleDelete,
  };
};