import * as React from 'react';
import {App} from '@app/index';
import {shallow} from 'enzyme';

describe('App tests', () => {
  test('should render default App component', () => {
    const view = shallow(<App />);
    expect(view).toMatchSnapshot();
  });
});
