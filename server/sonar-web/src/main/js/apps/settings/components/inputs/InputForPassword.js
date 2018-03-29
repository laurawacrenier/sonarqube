/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import React from 'react';
import { translate } from '../../../../helpers/l10n';
import { defaultInputPropTypes } from '../../propTypes';

export default class InputForPassword extends React.PureComponent {
  static propTypes = defaultInputPropTypes;

  state = {
    value: '',
    changing: false
  };

  componentWillReceiveProps(nextProps /*: Props*/) {
    if (!nextProps.hasValueChanged && this.props.hasValueChanged) {
      this.setState({ changing: false, value: '' });
    }
  }

  handleInputChange(e) {
    this.props.onChange(e.target.value);
    this.setState({ changing: true, value: e.target.value });
  }

  handleChangeClick(e) {
    e.preventDefault();
    e.target.blur();
    this.setState({ changing: true });
  }

  renderInput() {
    return (
      <form>
        <input className="hidden" type="password" />
        <input
          autoComplete="off"
          autoFocus={this.state.changing}
          className="js-password-input settings-large-input text-top"
          name={this.props.name}
          onChange={e => this.handleInputChange(e)}
          type="password"
          value={this.state.value}
        />
      </form>
    );
  }

  render() {
    const hasValue = !!this.props.value;

    if (this.state.changing || !hasValue) {
      return this.renderInput();
    }

    return (
      <div>
        <i className="big-spacer-right icon-lock icon-gray" />
        <button onClick={e => this.handleChangeClick(e)}>{translate('change_verb')}</button>
      </div>
    );
  }
}
