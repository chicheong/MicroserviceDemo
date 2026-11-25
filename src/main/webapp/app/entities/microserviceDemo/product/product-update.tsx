import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ProductStatus } from 'app/shared/model/enumerations/product-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './product.reducer';

export const ProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productEntity = useAppSelector(state => state.microservicedemo.product.entity);
  const loading = useAppSelector(state => state.microservicedemo.product.loading);
  const updating = useAppSelector(state => state.microservicedemo.product.updating);
  const updateSuccess = useAppSelector(state => state.microservicedemo.product.updateSuccess);
  const productStatusValues = Object.keys(ProductStatus);

  const handleClose = () => {
    navigate(`/microservicedemo/product${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...productEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdDate: displayDefaultDateTime(),
          lastModifiedDate: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVE',
          ...productEntity,
          createdDate: convertDateTimeFromServer(productEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(productEntity.lastModifiedDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="microserviceDemoApp.microserviceDemoProduct.home.createOrEditLabel" data-cy="ProductCreateUpdateHeading">
            <Translate contentKey="microserviceDemoApp.microserviceDemoProduct.home.createOrEditLabel">Create or edit a Product</Translate>
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
                  id="product-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.name')}
                id="product-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.code')}
                id="product-code"
                name="code"
                data-cy="code"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.brand')}
                id="product-brand"
                name="brand"
                data-cy="brand"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.description')}
                id="product-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.content')}
                id="product-content"
                name="content"
                data-cy="content"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.remark')}
                id="product-remark"
                name="remark"
                data-cy="remark"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.status')}
                id="product-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {productStatusValues.map(productStatus => (
                  <option value={productStatus} key={productStatus}>
                    {translate(`microserviceDemoApp.ProductStatus.${productStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.createdBy')}
                id="product-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.createdDate')}
                id="product-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.lastModifiedBy')}
                id="product-lastModifiedBy"
                name="lastModifiedBy"
                data-cy="lastModifiedBy"
                type="text"
              />
              <ValidatedField
                label={translate('microserviceDemoApp.microserviceDemoProduct.lastModifiedDate')}
                id="product-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button
                as={Link as any}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/microservicedemo/product"
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

export default ProductUpdate;
