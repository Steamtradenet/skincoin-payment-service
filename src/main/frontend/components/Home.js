import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { withRouter } from 'react-router-dom';

import NavigationBar from './NavigationBar';
import AppTable from './AppTable';
import EthereumInfo from './EthereumInfo';

import * as loginActions from '../actions/LoginActions';


class Home extends Component {

    constructor(props, context) {
        super(props, context);

        // Check authorization
        const { checkAuth } = this.props.actions;
        checkAuth(this.props.history);

        this.onLogout = this.onLogout.bind(this);
    }

    //componentWillReceiveProps(nextProps) {
    //    // // Check authorization
    //    if(nextProps.authenticated && (!nextProps.user || !nextProps.user.id)) {
    //        const { checkAuth } = this.props.actions;
    //        checkAuth(this.props.history);
    //    }
    //}

    onLogout() {
        const { logout } = this.props.actions;
        logout(this.props.history);
    }

    render() {
        const { authenticated } = this.props;

        if (authenticated && this.props.user) {
            return (
                <div>
                    <NavigationBar userName={this.props.user.name} onLogout={this.onLogout}/>

                    <div className='col-md-12'>
                        <div className='panel panel-default'>
                            <div className='panel-heading'>
                                <EthereumInfo/>
                            </div>
                            <div className='panel-body'>
                                <AppTable/>
                            </div>
                        </div>
                    </div>
                </div>
            )
        } else {
            return <div>Error</div>
        }
    }
}




//<div>You are authenticated :)</div>

//<button onClick={() => this.onLogout()}> Logout </button>

//const Home = ({ actions: { logout }, user, authenticated }) => (
//    <div>
//        <h3>Welcome</h3>
//        <h5>{authenticated ? 'You are authenticated :)' : 'Error'}</h5>
//    </div>
//
//
//    <button onClick={() => logout(history)}
//>
//LOGOUT
//</button>
//);

//const { object, bool } = PropTypes;
//
//Home.propTypes = {
//    actions: object.isRequired,
//    user: object,
//    authenticated: bool.isRequired
//};
//
const mapStateProps = (state) => {
    return {
        user: state.session.user,
        authenticated: state.session.authenticated
    }
};
//
const mapDispatchProps = (dispatch) => {
    return {
        actions: bindActionCreators(loginActions, dispatch)
    };
};

export default withRouter(connect(mapStateProps, mapDispatchProps)(Home));