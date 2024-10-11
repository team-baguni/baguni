import { NodeData } from '@/shared/types';
import { createNode } from '@/features/createNode';
import { moveNode } from '@/features/moveNode';
import { useGetStructure } from '@/features/folderManagement/hooks/useGetStructure';
import { useTreeStore } from '@/shared/stores/treeStore';
import { CreateHandler, MoveHandler, NodeApi } from 'react-arborist';
import { useCreateFolder } from '@/features/folderManagement/hooks/useCreateFolder';
import { useMoveFolder } from '@/features/folderManagement/hooks/useMoveFolder';
import { useGetDefaultFolderData } from '@/features/folderManagement/hooks/useGetDefaultFolderData';
import { useRenameFolder } from '@/features/folderManagement/hooks/useRenameFolder';
import { removeByIdFromStructure } from '@/features/treeStructureCRUD/removeByIdFromStructure';

export const useTreeHandlers = () => {
  const { data: structureData, refetch: refetchStructure } = useGetStructure();
  const {
    treeRef,
    focusedNode,
    focusedFolderNodeList,
    focusedLinkNodeList,
    setFocusedLinkNodeList,
    setFocusedFolderNodeList,
  } = useTreeStore();
  const { data: defaultFolderIdData } = useGetDefaultFolderData();
  const { mutateAsync: createFolder } = useCreateFolder();
  const { mutateAsync: moveFolder } = useMoveFolder();
  const { mutateAsync: renameFolder } = useRenameFolder();

  // const userId = defaultFolderIdData?.userId;
  const rootFolderId = defaultFolderIdData?.ROOT;
  const recycleBinId = defaultFolderIdData?.RECYCLE_BIN;
  // const unclassifiedId = defaultFolderIdData?.UNCLASSIFIED;
  let newFolderId = '';

  const handleCreate: CreateHandler<NodeData> = async ({
    parentId,
    parentNode,
    index,
    type,
  }): Promise<{ id: string }> => {
    console.log('parentId', parentId);
    console.log('parentNode', parentNode);
    console.log('index', index);
    console.log('type', type);

    // 로컬 폴더 구조 생성
    // const tempStructure = [...structureData!.root];
    //
    //   const newStructure = addNodeToStructure(
    //       tempStructure,
    //       parentId!,
    //       index,
    //       newNode
    //   );

    try {
      // 폴더 생성 (서버)
      const newFolderData = await createFolder('New Folder29');
      console.log('Server: Folder created:', newFolderData);
      newFolderId = newFolderData.id.toString();

      // 폴더 생성 (클라이언트)
      const updatedTreeData = createNode(
        structureData!.root,
        focusedNode,
        type,
        treeRef.current!,
        parentId,
        parentNode,
        index,
        newFolderData.id,
        newFolderData.name
      );

      // 서버에 업데이트된 트리 전송
      const serverData = {
        parentFolderId: rootFolderId,
        structure: {
          root: updatedTreeData,
          recycleBin: [],
        },
      };
      console.log('defaultFolderIdData', defaultFolderIdData);
      console.log('서버용 data', serverData);
      // 폴더 이동 (서버)
      await moveFolder({
        folderId: newFolderData.id.toString(),
        structure: serverData,
      });

      // 구조 데이터 새로 가져오기
      refetchStructure();
    } catch (error) {
      console.error('Error creating folder:', error);
    }
    return { id: newFolderId };
  };

  const handleDrag: MoveHandler<NodeData> = async ({
    dragIds,
    dragNodes,
    parentId,
    parentNode,
    index,
  }) => {
    const updatedTreeData = moveNode(
      structureData!.root,
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
      parentFolderId: parentNode
        ? parentNode.data.folderId
        : defaultFolderIdData!.ROOT,
      structure: {
        root: updatedTreeData,
        recycleBin: structureData!.recycleBin,
      },
    };
    console.log('defaultFolderIdData', defaultFolderIdData);
    console.log('서버에 보낼 데이터', serverData);

    await moveFolder({
      folderId: dragNodes[0].data.folderId!.toString(),
      structure: serverData,
    });
    refetchStructure();
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
    await renameFolder({ folderId: realNodeId.toString(), name });
    refetchStructure();
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

    const updatedRecycleBin = [...structureData!.recycleBin];
    updatedRecycleBin.push(nodes[0].data);

    const updatedTreeStructure = [...structureData!.root];
    removeByIdFromStructure(updatedTreeStructure, ids[0]);

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
    refetchStructure();
  };

  // 다른 곳으로 옮길 임시 함수들

  return { handleCreate, handleDrag, handleRename, handleDelete };
};
