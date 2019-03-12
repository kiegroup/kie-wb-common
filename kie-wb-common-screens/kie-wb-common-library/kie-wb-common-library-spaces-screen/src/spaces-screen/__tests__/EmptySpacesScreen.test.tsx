import * as React from "react";
import { EmptySpacesScreen, Tile } from "../SpacesScreen";
import { shallow } from "enzyme";

describe("snapshot", () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  test("of empty spaces screen should match snapshot", () => {
    expect(
      shallow(<EmptySpacesScreen onAddSpace={jest.fn()} />)
    ).toMatchSnapshot();
  });
});
