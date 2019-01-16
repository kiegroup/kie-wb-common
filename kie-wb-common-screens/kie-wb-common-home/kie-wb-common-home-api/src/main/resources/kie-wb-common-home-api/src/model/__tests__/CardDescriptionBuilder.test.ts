import { CardDescriptionLinkElement, CardDescriptionTextElement } from "../CardDescription";
import { CardDescriptionBuilder } from "../CardDescriptionBuilder";

test("without links, should return only one element, with type text.", () => {
  const description = new CardDescriptionBuilder("You can click nowhere!").build();

  const expectedElements = [new CardDescriptionTextElement("You can click nowhere!")];

  expect(description.elements).toEqual(expectedElements);
});

test("with links and indexes in order, should return a description with elements in correct order.", () => {
  const description = new CardDescriptionBuilder("You can click at {0} and {1}!")
    .addLink("link1", "foo")
    .addLink("link2", "bar")
    .build();

  const expectedElements = [
    new CardDescriptionTextElement("You can click at "),
    new CardDescriptionLinkElement("link1", "foo"),
    new CardDescriptionTextElement(" and "),
    new CardDescriptionLinkElement("link2", "bar"),
    new CardDescriptionTextElement("!")
  ];

  expect(description.elements).toEqual(expectedElements);
});

test("with links and indexes mixed order, should return a description with elements in correct order.", () => {
  const description = new CardDescriptionBuilder("You can click at {2} and {0} and {1}!")
    .addLink("link0", "target0")
    .addLink("link1", "target1")
    .addLink("link2", "target2")
    .build();

  const expectedElements = [
    new CardDescriptionTextElement("You can click at "),
    new CardDescriptionLinkElement("link2", "target2"),
    new CardDescriptionTextElement(" and "),
    new CardDescriptionLinkElement("link0", "target0"),
    new CardDescriptionTextElement(" and "),
    new CardDescriptionLinkElement("link1", "target1"),
    new CardDescriptionTextElement("!")
  ];

  expect(description.elements).toEqual(expectedElements);
});
