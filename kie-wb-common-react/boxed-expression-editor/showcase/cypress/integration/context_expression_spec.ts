/// <reference types="Cypress" />

describe("Context Expression Tests", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("Define context expression", () => {
    function ouiaId(id: string): string {
      return `[data-ouia-component-id='${id}']`;
    }

    // Entry point for each new expression
    cy.get(ouiaId("expression-container")).click();

    // Define new expression as Context
    cy.get(ouiaId("expression-popover-menu")).contains("Context").click();

    // Assert some content
    cy.get(ouiaId("OUIA-Generated-Table-1")).should("contain.text", "ContextEntry-1");
  });
});
