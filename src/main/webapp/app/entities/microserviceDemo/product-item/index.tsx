import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductItem from './product-item';
import ProductItemDeleteDialog from './product-item-delete-dialog';
import ProductItemDetail from './product-item-detail';
import ProductItemUpdate from './product-item-update';

const ProductItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductItem />} />
    <Route path="new" element={<ProductItemUpdate />} />
    <Route path=":id">
      <Route index element={<ProductItemDetail />} />
      <Route path="edit" element={<ProductItemUpdate />} />
      <Route path="delete" element={<ProductItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductItemRoutes;
