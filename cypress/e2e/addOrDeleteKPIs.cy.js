/* eslint-disable @stylistic/indent */
describe('add or delete KPIs', () => {
    beforeEach(() => {
        cy.login('yennefer@gmail.com', '12345678');
    });

    it('should add a kpi to the dashboard', () => {
        cy.visit('http://localhost:5173/dashboard');

        cy.get('[data-cy=edit-btn]').click();

        cy.get('[data-cy=kpi-select]').select(1);

        cy.get('[data-cy=save-btn]').click();
        cy.get('[data-cy=kpi]').should('have.length', 4);
    });

    it('should delete a kpi from the dashboard', () => {
        cy.visit('http://localhost:5173/dashboard');

        cy.get('[data-cy=edit-btn]').click();

        cy.get('[data-cy=remove-btn]').first().click();

        cy.get('[data-cy=save-btn]').click();
        cy.get('[data-cy=kpi]').should('have.length', 2);
    });
});