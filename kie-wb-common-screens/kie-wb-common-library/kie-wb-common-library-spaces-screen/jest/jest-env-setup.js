/*
 *  Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import { configure } from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({ adapter: new Adapter() });

// Mock JavaEnum base class
class MockJavaEnum {
  constructor(name) {
    this.name = name;
  }
}

// Mock the appformer-js module
jest.mock("appformer-js", () => ({
  translate: jest.fn((key) => key),
  JavaEnum: MockJavaEnum
}));

// Create a mock AppFormer instance for any code that uses the global
const mockAppFormer = {
  translate: jest.fn((key) => key)
};

// Replace the singleton
window.AppFormerInstance = mockAppFormer;