import React, { useEffect, useState } from 'react';
import { Button, Col, Form, FormControl, InputGroup, Row, Table } from 'react-bootstrap';
import { Translate, getPaginationState, translate } from 'react-jhipster';
import { Link, useLocation } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import InfiniteScroll from 'react-infinite-scroll-component';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

import { getEntities, reset, searchEntities } from './product-item.reducer';

export const ProductItem = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const productItemList = useAppSelector(state => state.microservicedemo.productItem.entities);
  const loading = useAppSelector(state => state.microservicedemo.productItem.loading);
  const links = useAppSelector(state => state.microservicedemo.productItem.links);
  const updateSuccess = useAppSelector(state => state.microservicedemo.productItem.updateSuccess);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(reset());
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    dispatch(reset());
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if (globalThis.scrollY > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting, search]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="product-item-heading" data-cy="ProductItemHeading">
        <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.home.title">Product Items</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/microservicedemo/product-item/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.home.createLabel">Create new Product Item</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <InputGroup>
              <FormControl
                type="text"
                name="search"
                defaultValue={search}
                onChange={handleSearch}
                placeholder={translate('microserviceDemoApp.microserviceDemoProductItem.home.search')}
              />
              <Button className="input-group-addon">
                <FontAwesomeIcon icon="search" />
              </Button>
              <Button type="reset" className="input-group-addon" onClick={clear}>
                <FontAwesomeIcon icon="trash" />
              </Button>
            </InputGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={productItemList ? productItemList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {productItemList?.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('code')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.code">Code</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                  </th>
                  <th className="hand" onClick={sort('isDefault')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.isDefault">Is Default</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('isDefault')} />
                  </th>
                  <th className="hand" onClick={sort('quantity')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.quantity">Quantity</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('quantity')} />
                  </th>
                  <th className="hand" onClick={sort('currency')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.currency">Currency</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('currency')} />
                  </th>
                  <th className="hand" onClick={sort('price')}>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.price">Price</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                  </th>
                  <th>
                    <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.product">Product</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {productItemList.map(productItem => (
                  <tr key={`entity-${productItem.id}`} data-cy="entityTable">
                    <td>
                      <Button as={Link as any} to={`/microservicedemo/product-item/${productItem.id}`} variant="link" size="sm">
                        {productItem.id}
                      </Button>
                    </td>
                    <td>{productItem.code}</td>
                    <td>{productItem.isDefault ? 'true' : 'false'}</td>
                    <td>{productItem.quantity}</td>
                    <td>{productItem.currency}</td>
                    <td>{productItem.price}</td>
                    <td>
                      {productItem.product ? (
                        <Link to={`/microservicedemo/product/${productItem.product.id}`}>{productItem.product.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          as={Link as any}
                          to={`/microservicedemo/product-item/${productItem.id}`}
                          variant="info"
                          size="sm"
                          data-cy="entityDetailsButton"
                        >
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          as={Link as any}
                          to={`/microservicedemo/product-item/${productItem.id}/edit`}
                          variant="primary"
                          size="sm"
                          data-cy="entityEditButton"
                        >
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          onClick={() => (globalThis.location.href = `/microservicedemo/product-item/${productItem.id}/delete`)}
                          variant="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.home.notFound">No Product Items found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default ProductItem;
