import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './product.reducer';

export const ProductDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const productEntity = useAppSelector(state => state.microservicedemo.product.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productDetailsHeading">
          <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.detail.title">Product</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.name">Name</Translate>
            </span>
          </dt>
          <dd>{productEntity.name}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.code">Code</Translate>
            </span>
          </dt>
          <dd>{productEntity.code}</dd>
          <dt>
            <span id="brand">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.brand">Brand</Translate>
            </span>
          </dt>
          <dd>{productEntity.brand}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.description">Description</Translate>
            </span>
          </dt>
          <dd>{productEntity.description}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.content">Content</Translate>
            </span>
          </dt>
          <dd>{productEntity.content}</dd>
          <dt>
            <span id="remark">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.remark">Remark</Translate>
            </span>
          </dt>
          <dd>{productEntity.remark}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.status">Status</Translate>
            </span>
          </dt>
          <dd>{productEntity.status}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{productEntity.createdBy}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {productEntity.createdDate ? <TextFormat value={productEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedBy">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.lastModifiedBy">Last Modified By</Translate>
            </span>
          </dt>
          <dd>{productEntity.lastModifiedBy}</dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {productEntity.lastModifiedDate ? (
              <TextFormat value={productEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/microservicedemo/product" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/microservicedemo/product/${productEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductDetail;
