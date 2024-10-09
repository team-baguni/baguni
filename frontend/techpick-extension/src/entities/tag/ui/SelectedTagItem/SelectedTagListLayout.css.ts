import { style, styleVariants } from '@vanilla-extract/css';
import { SelectedTagCommonStyle } from './SelectedTagCommonStyle.css';

export const ListLayoutHeightVariants = styleVariants({
  fixed: {
    overflow: 'hidden',
    maxHeight: '60px',
  },
  flexible: {
    overflow: 'visible',
  },
});

export type ListLayoutHeightVariantKeyTypes =
  keyof typeof ListLayoutHeightVariants;

export const SelectedTagListLayoutStyle = style({
  display: 'flex',
  gap: '4px',
  flexWrap: 'wrap',
  padding: '4px',
  width: SelectedTagCommonStyle.width,
});