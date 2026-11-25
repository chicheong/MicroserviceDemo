import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './product-item.reducer';

export const ProductItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productItemEntity = useAppSelector(state => state.microservicedemo.productItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productItemDetailsHeading">
          <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.detail.title">ProductItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.code">Code</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.code}</dd>
          <dt>
            <span id="isDefault">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.isDefault">Is Default</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.isDefault ? 'true' : 'false'}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.quantity}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.currency}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.price">Price</Translate>
            </span>
          </dt>
          <dd>{productItemEntity.price}</dd>
          <dt>
            <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.product">Product</Translate>
          </dt>
          <dd>{productItemEntity.product ? productItemEntity.product.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/microservicedemo/product-item" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/microservicedemo/product-item/${productItemEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductItemDetail;
