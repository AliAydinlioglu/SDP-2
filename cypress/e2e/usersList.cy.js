describe('UsersList', () => {
  beforeEach(() => {
    cy.login('geralt@gmail.com', '12345678');

    cy.fixture('users').then((users) => {
      cy.intercept('GET', '/users', users);
    });
    cy.visit('http://localhost:5173/users');
  });

  it('should display a list of users', () => {
    cy.get('table.user-table tbody tr').should('have.length', 2);
  });

  it('should filter users by name', () => {
    cy.get('[data-cy=user-search-bar]').type('John');
    cy.get('table.user-table tbody tr').should('have.length', 1);
    cy.get('table.user-table tbody tr').first().should('contain', 'John Doe');
  });

  it('should navigate to add user page on add button click', () => {
    cy.get('[data-cy=add-user-btn]').click();
    cy.url().should('include', '/users/add');
  });
});
