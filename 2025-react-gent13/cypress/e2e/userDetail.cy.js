describe('UserDetail', () => {
  beforeEach(() => {
    cy.login('geralt@gmail.com', '12345678');

    cy.fixture('users').then((users) => {
      cy.intercept('GET', '/users/1', users[0]);
    });
    cy.visit('http://localhost:5173/users/1');
  });

  it('should display user details', () => {
    cy.get('[data-cy=user-details-title]').should('contain', 'User Details');
    cy.get('[data-cy=user-name]').should('contain', 'John Doe');
    cy.get('[data-cy=user-email]').should('contain', 'john.doe@example.com');
  });

  it('should navigate to edit page on edit button click', () => {
    cy.get('[data-cy=edit-btn]').click();
    cy.url().should('include', '/users/edit/1');
  });

  it('should show delete confirmation modal on delete button click', () => {
    cy.get('[data-cy=delete-btn]').click();
    cy.get('[data-cy=delete-modal-title]').should('contain', 'Delete User 1');
  });
});
