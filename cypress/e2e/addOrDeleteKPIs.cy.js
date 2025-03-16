/* eslint-disable @stylistic/indent */
describe('add or delete KPIs', () => {
    beforeEach(() => {
        cy.login('geralt@gmail.com', '12345678');
    });

    it('should add a kpi to the dashboard', () => {
        cy.visit('http://localhost:5173/dashboard');

        cy.get('[data-cy=edit-btn]').click();

        cy.get('[data-cy=kpi-select]').select('Fake Graph');

        cy.get('[data-cy=save-btn]').click();
        cy.get('[data-cy=kpi]').should('have.length', 4);
    });
});