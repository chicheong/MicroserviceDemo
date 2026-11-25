import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('ProductItem e2e test', () => {
  const productItemPageUrl = '/microservicedemo/product-item';
  let username: string;
  let password: string;
  const productItemSample = {};

  let productItem;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/microservicedemo/api/product-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/microservicedemo/api/product-items').as('postEntityRequest');
    cy.intercept('DELETE', '/services/microservicedemo/api/product-items/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/microservicedemo/api/product-items/${productItem.id}`,
      }).then(() => {
        productItem = undefined;
      });
    }
  });

  it('ProductItems menu should load ProductItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('microservicedemo/product-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductItem').should('exist');
    cy.location('pathname').should('eq', productItemPageUrl);
  });

  describe('ProductItem page', () => {
    it('should have translated page title', () => {
      cy.visit(productItemPageUrl);
      cy.getEntityHeading('ProductItem').should('not.contain', 'microserviceDemoApp.microserviceDemoProductItem.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${productItemPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('ProductItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productItemPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/microservicedemo/api/product-items',
          body: productItemSample,
        }).then(({ body }) => {
          productItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/microservicedemo/api/product-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/microservicedemo/api/product-items?page=0&size=20>; rel="last",<http://localhost/services/microservicedemo/api/product-items?page=0&size=20>; rel="first"',
              },
              body: [productItem],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productItemPageUrl);
      });

      it('edit button click should load edit ProductItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productItemPageUrl);
      });

      it('edit button click should load edit ProductItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productItemPageUrl);
      });

      it('last delete button click should delete instance of ProductItem', () => {
        cy.intercept('GET', '/services/microservicedemo/api/product-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('productItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productItemPageUrl);

        productItem = undefined;
      });
    });
  });

  describe('new ProductItem page', () => {
    beforeEach(() => {
      cy.visit(productItemPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductItem');
    });

    it('should create an instance of ProductItem', () => {
      cy.get(`[data-cy="code"]`).type('role cash');
      cy.get(`[data-cy="code"]`).should('have.value', 'role cash');

      cy.get(`[data-cy="isDefault"]`).should('not.be.checked');
      cy.get(`[data-cy="isDefault"]`).click();
      cy.get(`[data-cy="isDefault"]`).should('be.checked');

      cy.get(`[data-cy="quantity"]`).type('26162');
      cy.get(`[data-cy="quantity"]`).should('have.value', '26162');

      cy.get(`[data-cy="currency"]`).type('hmph although tool');
      cy.get(`[data-cy="currency"]`).should('have.value', 'hmph although tool');

      cy.get(`[data-cy="price"]`).type('19517.6');
      cy.get(`[data-cy="price"]`).should('have.value', '19517.6');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', productItemPageUrl);
    });
  });
});
