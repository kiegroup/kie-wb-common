import * as React from "react";
import { SpacesScreen } from "../SpacesScreen";
import { shallow, ShallowWrapper } from "enzyme";
import { AppFormer } from "appformer-js";

describe("snapshot", () => {
  function newSpacesScreen() {
    return shallow(<SpacesScreen exposing={jest.fn()} />, {
      disableLifecycleMethods: true
    });
  }

  beforeEach(() => {
    jest.resetAllMocks();
    SpacesScreen.prototype.canCreateSpace = () => true;
  });

  test("of spaces screen with no space but loading", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: true,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with some spaces but no permission to create spaces", () => {
    SpacesScreen.prototype.canCreateSpace = () => false;
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: [{ name: "Foo", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with some spaces", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: false,
      loading: false,
      spaces: [{ name: "Foo", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space but loading and popup open", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: true,
      loading: true,
      spaces: []
    });
    expect(spacesScreen).toMatchSnapshot();
  });

  test("of spaces screen with no space and popup open", () => {
    const spacesScreen = newSpacesScreen();
    spacesScreen.setState({
      newSpacePopupOpen: true,
      loading: false,
      spaces: [{ name: "Foo", contributors: [], repositories: [] }]
    });
    expect(spacesScreen).toMatchSnapshot();
  });
});
