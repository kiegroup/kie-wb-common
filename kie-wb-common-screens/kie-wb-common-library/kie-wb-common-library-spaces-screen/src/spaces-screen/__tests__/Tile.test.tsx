import * as React from "react";
import { Tile } from "../SpacesScreen";
import { shallow } from "enzyme";

describe("snapshot", () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  test("of tile should match snapshot", () => {
    expect(
      shallow(
        <Tile
          space={{ name: "Foo", contributors: [], repositories: [] }}
          onSelect={jest.fn()}
        />
      )
    ).toMatchSnapshot();
  });
});
