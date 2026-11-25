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

describe('Product e2e test', () => {
  const productPageUrl = '/microservicedemo/product';
  let username: string;
  let password: string;
  const productSample = { name: 'indeed and' };

  let product;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/microservicedemo/api/products+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/microservicedemo/api/products').as('postEntityRequest');
    cy.intercept('DELETE', '/services/microservicedemo/api/products/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/microservicedemo/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });

  it('Products menu should load Products page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('microservicedemo/product');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Product').should('exist');
    cy.location('pathname').should('eq', productPageUrl);
  });

  describe('Product page', () => {
    it('should have translated page title', () => {
      cy.visit(productPageUrl);
      cy.getEntityHeading('Product').should('not.contain', 'microserviceDemoApp.microserviceDemoProduct.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Product page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${productPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/microservicedemo/api/products',
          body: productSample,
        }).then(({ body }) => {
          product = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/microservicedemo/api/products+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/microservicedemo/api/products?page=0&size=20>; rel="last",<http://localhost/services/microservicedemo/api/products?page=0&size=20>; rel="first"',
              },
              body: [product],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Product page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('product');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productPageUrl);
      });

      it('edit button click should load edit Product page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productPageUrl);
      });

      it('edit button click should load edit Product page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productPageUrl);
      });

      it('last delete button click should delete instance of Product', () => {
        cy.intercept('GET', '/services/microservicedemo/api/products/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('product').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', productPageUrl);

        product = undefined;
      });
    });
  });

  describe('new Product page', () => {
    beforeEach(() => {
      cy.visit(productPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Product');
    });

    it('should create an instance of Product', () => {
      cy.get(`[data-cy="name"]`).type('whether narrow nor');
      cy.get(`[data-cy="name"]`).should('have.value', 'whether narrow nor');

      cy.get(`[data-cy="code"]`).type('pink');
      cy.get(`[data-cy="code"]`).should('have.value', 'pink');

      cy.get(`[data-cy="brand"]`).type('ha tool past');
      cy.get(`[data-cy="brand"]`).should('have.value', 'ha tool past');

      cy.get(`[data-cy="description"]`).type('mmm till phew');
      cy.get(`[data-cy="description"]`).should('have.value', 'mmm till phew');

      cy.get(`[data-cy="content"]`).type('abacus fleck suffice');
      cy.get(`[data-cy="content"]`).should('have.value', 'abacus fleck suffice');

      cy.get(`[data-cy="remark"]`).type('early lampoon');
      cy.get(`[data-cy="remark"]`).should('have.value', 'early lampoon');

      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(`[data-cy="createdBy"]`).type('clearly unselfish');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'clearly unselfish');

      cy.get(`[data-cy="createdDate"]`).type('2026-11-24T15:17');
      cy.get(`[data-cy="createdDate"]`).blur();
      cy.get(`[data-cy="createdDate"]`).should('have.value', '2026-11-24T15:17');

      cy.get(`[data-cy="lastModifiedBy"]`).type('next');
      cy.get(`[data-cy="lastModifiedBy"]`).should('have.value', 'next');

      cy.get(`[data-cy="lastModifiedDate"]`).type('2026-11-24T09:29');
      cy.get(`[data-cy="lastModifiedDate"]`).blur();
      cy.get(`[data-cy="lastModifiedDate"]`).should('have.value', '2026-11-24T09:29');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        product = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', productPageUrl);
    });
  });
});
