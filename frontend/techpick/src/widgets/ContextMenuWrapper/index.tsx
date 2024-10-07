import React from 'react';
import * as ContextMenu from '@radix-ui/react-context-menu';
import { ChevronRightIcon } from '@radix-ui/react-icons';

// Vanilla Extract 스타일 import
import {
  ContextMenuTrigger,
  ContextMenuContent,
  ContextMenuItem,
  RightSlot,
  ContextMenuSubContent,
  ContextMenuSubTrigger,
} from './contextMenu.css';
import { useTreeStore } from '@/shared/stores/treeStore'; // 기존 Vanilla Extract 스타일 import

interface ContextMenuWrapperProps {
  children: React.ReactNode;
}

export function EditorContextMenu({ children }: ContextMenuWrapperProps) {
  const { focusedNode } = useTreeStore();

  return (
    <ContextMenu.Root>
      <ContextMenu.Trigger className={ContextMenuTrigger}>
        {children}
      </ContextMenu.Trigger>
      <ContextMenu.Portal>
        <ContextMenu.Content className={ContextMenuContent}>
          {focusedNode?.data.type === 'folder' && (
            <ContextMenu.Sub>
              <ContextMenu.SubTrigger className={ContextMenuSubTrigger}>
                New
                <div className={RightSlot}>
                  <ChevronRightIcon />
                </div>
              </ContextMenu.SubTrigger>
              <ContextMenu.Portal>
                <ContextMenu.SubContent
                  className={ContextMenuSubContent}
                  sideOffset={2}
                  alignOffset={-5}
                >
                  <ContextMenu.Item className={ContextMenuItem}>
                    Folder <div className={RightSlot}></div>
                  </ContextMenu.Item>

                  <ContextMenu.Item className={ContextMenuItem}>
                    Pick
                  </ContextMenu.Item>
                </ContextMenu.SubContent>
              </ContextMenu.Portal>
            </ContextMenu.Sub>
          )}

          <ContextMenu.Item className={ContextMenuItem}>
            Rename <div className={RightSlot}></div>
          </ContextMenu.Item>
          <ContextMenu.Item className={ContextMenuItem}>
            Delete <div className={RightSlot}></div>
          </ContextMenu.Item>
        </ContextMenu.Content>
      </ContextMenu.Portal>
    </ContextMenu.Root>
  );
}
