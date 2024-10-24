import { style } from '@vanilla-extract/css';
import { colorThemeContract, commonTheme } from 'techpick-shared';

export const linkEditorLabel = style({
  display: 'flex',
  gap: commonTheme.space[8],
  fontWeight: 370,
});

export const searchSection = style({
  display: 'flex',
  gap: commonTheme.space[8],
});

export const linkEditorSection = style({
  width: '100%',
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
});

export const linkEditorHeader = style({
  width: '100%',
  height: '56px',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  borderBottom: `1px solid ${colorThemeContract.color.border}`,
  padding: `0 ${commonTheme.space[16]}`,
});

export const linkEditor = style({
  width: '100%',
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  overflow: 'scroll',
});

export const folderViewSection = style({
  width: '100%',
  display: 'flex',
  flexWrap: 'wrap',
  borderBottom: `1px solid ${colorThemeContract.color.border}`,
  padding: `${commonTheme.space[8]} ${commonTheme.space[16]}`,
});

export const linkViewSection = style({
  width: '100%',
  display: 'flex',
  flexWrap: 'wrap',
  padding: `${commonTheme.space[8]} ${commonTheme.space[16]}`,
});

export const linkEditorSectionFooter = style({
  width: '100%',
  height: '56px',
  borderTop: `1px solid ${colorThemeContract.color.border}`,
});