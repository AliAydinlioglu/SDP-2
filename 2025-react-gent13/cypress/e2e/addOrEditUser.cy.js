describe('AddOrEditUser', () => {
  beforeEach(() => {
    cy.login('geralt@gmail.com', '12345678');

    cy.fixture('users').then((users) => {
      cy.intercept('GET', '/users/1', users[0]);
    });
  });

  it('should display the add user form', () => {
    cy.visit('http://localhost:5173/users/add');
    cy.get('[data-cy=add-edit-user-title]').should('contain', 'Add User');
    cy.get('form').should('exist');
  });

  it('should display the edit user form', () => {
    cy.visit('http://localhost:5173/users/edit/1');
    cy.get('[data-cy=add-edit-user-title]').should('contain', 'Edit User');
    cy.get('form').should('exist');

  });
});
