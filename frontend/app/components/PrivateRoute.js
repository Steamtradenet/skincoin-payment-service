import React from 'react';
import PropTypes from 'prop-types';
import { Route, Redirect } from 'react-router-dom';

const PrivateRoute = ({ component, exact=false, path, authenticated }) => (
    <Route
        exact={exact}
        path={path}
        render={props => (
      authenticated ? (
        React.createElement(component, props)
        ) : (
        <Redirect to={{
          pathname: '/login',
          state: { from: props.location }
        }}/>
      )
    )}
        />
);

PrivateRoute.propTypes = {
    component: PropTypes.func.isRequired,
    exact: PropTypes.bool,
    path: PropTypes.string.isRequired,
    authenticated: PropTypes.bool.isRequired,
    location: PropTypes.object
};

export default PrivateRoute;