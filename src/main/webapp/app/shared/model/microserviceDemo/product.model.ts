import dayjs from 'dayjs';

import { ProductStatus } from 'app/shared/model/enumerations/product-status.model';

export interface IProduct {
  id?: number;
  name?: string;
  code?: string | null;
  brand?: string | null;
  description?: string | null;
  content?: string | null;
  remark?: string | null;
  status?: keyof typeof ProductStatus | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IProduct> = {};
