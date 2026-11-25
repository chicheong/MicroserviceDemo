import { loadMoreDataWhenScrolled } from 'react-jhipster';

import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { getPageNumberFromLinkHeader } from 'app/shared/jhipster/link-header';
import { IProductItem, defaultValue } from 'app/shared/model/microserviceDemo/product-item.model';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { cleanEntity } from 'app/shared/util/entity-utils';

const initialState: EntityState<IProductItem> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'services/microservicedemo/api/product-items';
const apiSearchUrl = 'services/microservicedemo/api/product-items/_search';

// Actions

export const searchEntities = createAsyncThunk(
  'productItem/search_entity',
  async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`;
    return axios.get<IProductItem[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntities = createAsyncThunk(
  'productItem/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${Date.now()}`;
    return axios.get<IProductItem[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntity = createAsyncThunk(
  'productItem/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IProductItem>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'productItem/create_entity',
  async (entity: IProductItem) => {
    return axios.post<IProductItem>(apiUrl, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'productItem/update_entity',
  async (entity: IProductItem) => {
    return axios.put<IProductItem>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'productItem/partial_update_entity',
  async (entity: IProductItem) => {
    return axios.patch<IProductItem>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'productItem/delete_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<IProductItem>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

// slice

export const ProductItemSlice = createEntitySlice({
  name: 'productItem',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const { data, headers } = action.payload;
        const links = getPageNumberFromLinkHeader(headers.link);

        return {
          ...state,
          loading: false,
          links,
          entities: loadMoreDataWhenScrolled(state.entities, data, links),
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = ProductItemSlice.actions;

// Reducer
export default ProductItemSlice.reducer;
