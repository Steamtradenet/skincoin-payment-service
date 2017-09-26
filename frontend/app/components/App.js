// This component handles the App template used on every page.
import React from 'react';
import PropTypes from 'prop-types';

import { BrowserRouter as Router, Route } from 'react-router-dom';
import { connect } from 'react-redux';
import Home from './Home';
import Login from './Login';
import PrivateRoute from './PrivateRoute';

const App = ( { authenticated, isFetching } ) => (
    <Router>
        {
            !isFetching &&
        <div>
            <PrivateRoute exact path="/" component={Home} authenticated={authenticated}/>
            <Route path="/login" component={Login}/>
        </div>
        }
    </Router>
);

App.propTypes = {
    authenticated: PropTypes.bool.isRequired,
    isFetching: PropTypes.bool.isRequired,
    user: PropTypes.object
};

const mapState = ({ session }) => ({
    authenticated: session.authenticated,
    isFetching: session.isFetching,
    user: session.user
});

export default connect(mapState)(App);


//export default App;

//export default connect(
//    state => ({
//
//        testStore: state
//
//    }),
//    dispatch => ({})
//)(App);

//const mapState = ({ session }) => ({
//    checked: session.checked,
//    authenticated: session.authenticated
//});
//
//export default connect(mapState)(App);

//var App = React.createClass({
//
//    getInitialState: function () {
//        return {
//            username: "",
//            password: ""
//        };
//    },
//    _onSubmit: function (event) {
//
//        event.preventDefault();
//
//        var data = {
//            name: this.state.username,
//            password: this.state.password
//        };
//
//        fetch('/login', {
//            method: 'POST',
//            headers: {
//                'Accept': 'application/json',
//                'Content-Type': 'application/json'
//            },
//            body: JSON.stringify(data)
//        }).then( (response) => {
//            console.log(response);
//
//        }).catch( (data) => {
//            alert(data.responseJSON.message);
//        });
//    },
//    _onUserNameChange: function (event) {
//        this.setState({username: event.target.value});
//    },
//    _onPasswordChange: function (event) {
//        this.setState({password: event.target.value});
//    },
//    render: function() {
//        return (
//            <div>
//                <form action="/login" method="post">
//
//                    <input type='text' value={this.state.username} onChange={this._onUserNameChange} />
//
//                    <input type='password' />
//
//                    <button onClick={this._onSubmit} type="submit" name="submit" >
//                        Login
//                    </button>
//                </form>
//            </div>
//        );
//    }
//
//});
//
//export default App;


