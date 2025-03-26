describe('AddOrEditMachine', () => {
  beforeEach(() => {
    cy.login('geralt@gmail.com', '12345678');
    cy.fixture('machines').then((machines) => {
      cy.intercept('GET', '/machines/1', machines[0]);
      cy.intercept('GET', '/sites', [{ id: 1, name: 'Site A' }]);
      cy.intercept('GET', '/users', [{ id: 1, name: 'Technician A', rol: 'TECHNIEKER' }]);
    });
  });

  it('should display the add machine form', () => {
    cy.visit('http://localhost:5173/machines/add');
    cy.get('[data-cy=add-edit-machine-title]').should('contain', 'Add machine');
    cy.get('[data-cy=machine-form]').should('exist');
  });

  it('should display the edit machine form', () => {
    cy.visit('http://localhost:5173/machines/edit/1');
    cy.get('[data-cy=add-edit-machine-title]').should('contain', 'Edit machine');
    cy.get('[data-cy=machine-form]').should('exist');
  });
});
