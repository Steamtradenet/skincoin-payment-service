import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { withRouter } from 'react-router';
import * as loginActions from '../actions/LoginActions';
import Input from './Input';

class Login extends Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            user: {
                username: '',
                password: ''
            }
        };

        this.onSubmit = this.onSubmit.bind(this);
        this.onChange = this.onChange.bind(this);
    }

    onSubmit() {
        const { user } = this.state;
        const { loginUser } = this.props.actions;

        loginUser(user, this.props.history);
    }

    onChange(e) {
        const { value, name } = e.target;
        const { user } = this.state;
        user[name] = value;
        this.setState({ user });
    }

    render() {
        const { user: { username, password } } = this.state;

        return (
            <div className="wrapper">
                <form className="form-signin">
                    <h2 className="form-signin-heading">Please login</h2>

                    <input
                        className="form-control"
                        name="username"
                        value={username}
                        label="Username"
                        type="text"
                        onChange={this.onChange}
                        placeholder="User name" required=""
                        />
                    <input
                        className="form-control"
                        name="password"
                        value={password}
                        label="Password"
                        type="password"
                        onChange={this.onChange}
                        placeholder="Password" required=""
                        />
                    <span style={{color: 'red'}}>{this.props.errorMessage}</span>
                    <button
                        className="btn btn-lg btn-info btn-block"
                        onClick={() => this.onSubmit()}
                        type="submit">Submit
                    </button>

                    </form>
                </div>
        );



        //return (
        //    <div>
        //        <h3>LOGIN</h3>
        //        <Input
        //            name="username"
        //            value={username}
        //            label="Username"
        //            type="text"
        //            onChange={this.onChange}
        //            />
        //        <Input
        //            name="password"
        //            value={password}
        //            label="Password"
        //            type="password"
        //            onChange={this.onChange}
        //            />
        //        <button
        //            onClick={() => this.onSubmit()}
        //            type="submit">Submit
        //        </button>
        //
        //
        //    </div>
        //);
    }
}

Login.propTypes = {
    actions: PropTypes.object.isRequired
};

const mapDispatchProps = (dispatch) => {
    return {
        actions: bindActionCreators(loginActions, dispatch)
    };
};

const mapStateProps = (state) => {
    return {
        errorMessage: state.session.errorMessage
    };
};

export default withRouter(connect(mapStateProps, mapDispatchProps)(Login));