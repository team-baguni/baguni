import { useQueryClient } from '@tanstack/react-query';
import { NodeApi } from 'react-arborist';
import toast from 'react-hot-toast';
import { deleteFolder } from '@/components/nodeManagement/api/folder/folderQueryFunctions';
import { useGetDefaultFolderData } from '@/components/nodeManagement/api/folder/useGetDefaultFolderData';
import { useGetRootAndRecycleBinData } from '@/components/nodeManagement/api/folder/useGetRootAndRecycleBinData';
import { useMoveFolder } from '@/components/nodeManagement/api/folder/useMoveFolder';
import { useRenameFolder } from '@/components/nodeManagement/api/folder/useRenameFolder';
import { deletePick } from '@/components/nodeManagement/api/pick/pickQueryFunctions';
import { useMovePick } from '@/components/nodeManagement/api/pick/useMovePick';
import { createNode } from '@/components/nodeManagement/utils/createNode';
import { getCurrentTreeTypeByNode } from '@/components/nodeManagement/utils/getCurrentTreeTypeByNode';
import { getNewIdFromStructure } from '@/components/nodeManagement/utils/getNewIdFromStructure';
import { moveNode } from '@/components/nodeManagement/utils/moveNode';
import { removeByIdFromStructure } from '@/components/nodeManagement/utils/removeByIdFromStructure';
import { useTreeStore } from '@/stores/treeStore';
import { ApiStructureData } from '@/types/ApiTypes';
import type { CreateHandler, MoveHandler } from 'react-arborist';
import { NodeData } from '@/types';

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

  const { mutateAsync: movePick } = useMovePick();
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
    const isRoot = getCurrentTreeTypeByNode(dragNodes[0], treeRef) === 'root';
    const isPick = dragNodes[0].data.type === 'pick';
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

    if (isPick) {
      await movePick({
        pickId: dragNodes[0].data.pickId!.toString(),
        structure: serverData,
      });
    } else {
      await moveFolder({
        folderId: dragNodes[0].data.folderId!.toString(),
        structure: serverData,
      });
    }
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
      toast.error('이름이 중복됩니다.\n 다른 이름을 입력해주세요.');
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
    const isFolder = nodes[0].data.type === 'folder';
    const realNodeId = isFolder ? nodes[0].data.folderId : nodes[0].data.pickId;

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
    if (isFolder) {
      await moveFolder({
        folderId: realNodeId.toString(),
        structure: serverData,
      });
    } else {
      await movePick({
        pickId: realNodeId.toString(),
        structure: serverData,
      });
    }

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
    const isFolder = nodes[0].data.type === 'folder';
    const realNodeId = isFolder ? nodes[0].data.folderId : nodes[0].data.pickId;

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
    if (isFolder) {
      await deleteFolder({
        folderId: realNodeId.toString(),
        structure: serverData,
      });
    } else {
      await deletePick({
        pickId: realNodeId.toString(),
        structure: serverData,
      });
    }

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
