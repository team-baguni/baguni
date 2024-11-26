import { style } from '@vanilla-extract/css';
import { colorVars } from 'techpick-shared';

export const pickRecordHeaderLayoutStyle = style({
  display: 'flex',
  alignItems: 'center',
  width: '1044px',
  height: '24px',
  borderTop: '1px solid ',
  borderBottom: '1px solid ',
  borderColor: colorVars.gold7,
  marginTop: '24px',
});