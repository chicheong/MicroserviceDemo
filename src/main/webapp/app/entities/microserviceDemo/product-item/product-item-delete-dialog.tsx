import React, { useEffect, useState } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteEntity, getEntity } from './product-item.reducer';

export const ProductItemDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id!));
    setLoadModal(true);
  }, []);

  const productItemEntity = useAppSelector(state => state.microservicedemo.productItem.entity);
  const updateSuccess = useAppSelector(state => state.microservicedemo.productItem.updateSuccess);

  const handleClose = () => {
    navigate('/microservicedemo/product-item');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteEntity(productItemEntity.id));
  };

  return (
    <Modal show onHide={handleClose}>
      <ModalHeader data-cy="productItemDeleteDialogHeading" closeButton>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="microserviceDemoApp.microserviceDemoProductItem.delete.question">
        <Translate contentKey="microserviceDemoApp.microserviceDemoProductItem.delete.question" interpolate={{ id: productItemEntity.id }}>
          Are you sure you want to delete this ProductItem?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button variant="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-productItem" data-cy="entityConfirmDeleteButton" variant="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ProductItemDeleteDialog;
