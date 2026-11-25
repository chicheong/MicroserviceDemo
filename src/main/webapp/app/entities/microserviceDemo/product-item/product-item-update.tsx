import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getProducts } from 'app/entities/microserviceDemo/product/product.reducer';

import { createEntity, getEntity, updateEntity } from './product-item.reducer';

export const ProductItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const products = useAppSelector(state => state.microservicedemo.product.entities);
  const productItemEntity = useAppSelector(state => state.microservicedemo.productItem.entity);
  const loading = useAppSelector(state => state.microservicedemo.productItem.loading);
  const updating = useAppSelector(state => state.microservicedemo.productItem.updating);
  const updateSuccess = useAppSelector(state => state.microservicedemo.productItem.updateSuccess);

  const handleClose = () => {
    navigate('/microservicedemo/product-item');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getProducts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }

    const entity = {
      ...productItemEntity,
      ...values,
      product: products.find(it => it.id.toString() === values.product?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...productItemEntity,
          product: productItemEntity?.product?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="microserviceDemoApp.microserviceDemoProductItem.home.createOrEditLabel" data-cy="ProductItemCreateUpdateHeading">
            <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.home.createOrEditLabel">
              Create or edit a ProductItem
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="product-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProductItem.code')}
                id="product-item-code"
                name="code"
                data-cy="code"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProductItem.isDefault')}
                id="product-item-isDefault"
                name="isDefault"
                data-cy="isDefault"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProductItem.quantity')}
                id="product-item-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProductItem.currency')}
                id="product-item-currency"
                name="currency"
                data-cy="currency"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProductItem.price')}
                id="product-item-price"
                name="price"
                data-cy="price"
                type="text"
              />
              <ValidatedField
                id="product-item-product"
                name="product"
                data-cy="product"
                label={translate('microserviceDemoApp.microserviceDemoProductItem.product')}
                type="select"
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/microservicedemo/product-item"
                replace
                variant="info"
              >
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProductItemUpdate;
