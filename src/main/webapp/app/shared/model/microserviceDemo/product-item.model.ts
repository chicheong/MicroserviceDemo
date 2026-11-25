import { IProduct } from 'app/shared/model/microserviceDemo/product.model';

export interface IProductItem {
  id?: number;
  code?: string | null;
  isDefault?: boolean | null;
  quantity?: number | null;
  currency?: string | null;
  price?: number | null;
  product?: IProduct | null;
}

export const defaultValue: Readonly<IProductItem> = {
  isDefault: false,
};
