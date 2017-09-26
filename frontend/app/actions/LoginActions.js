import * as clientApi from '../api/ClientApi';

/****************************************************************/
/* LOGIN                                                        */
/****************************************************************/
export const LOGIN_REQUEST = 'LOGIN_REQUEST';
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGIN_FAILURE = 'LOGIN_FAILURE';

function requestLogin(creds) {
    return {
        type: LOGIN_REQUEST,
        payload: creds
    }
}

function receiveLogin(response) {
    return {
        type: LOGIN_SUCCESS,
        payload: response
    }
}

function loginError(message) {
    return {
        type: LOGIN_FAILURE,
        error: message
    }
}

export function loginUser(creds, history) {

    return (dispatch) => {

        dispatch(requestLogin(creds));

        return clientApi.loginUser(creds).then(response => {

            if (response.status && response.status == "FAIL") {
               dispatch(loginError(response.error));
            } else {
                localStorage.setItem('jwtToken', response.token);
                dispatch(receiveLogin(response));
                history.replace('/');
            }

        }).catch(error => {
            console.log(error);
            dispatch(loginError(error));
        });
    };
}


/****************************************************************/
/* LOGOUT                                                       */
/****************************************************************/
export const LOGOUT_SUCCESS = 'LOGOUT_SUCCESS';

function receiveLogout() {
    return {
        type: LOGOUT_SUCCESS
    }
}


export const checkAuth = (history) => {
    return (dispatch) => {

        return clientApi.checkAuth().then(response => {

            processResponse(response, history, dispatch,
                receiveLogin({user: response}));

        }).catch(error => {
            console.log(error);
            doLogout(dispatch, history);
        });

    }
};


export const logout = (history) => {
    return (dispatch) => {

        doLogout(dispatch, history);
    };
};


function processResponse(response, history, dispatch, successAction) {
    if (response.status && response.status == "FAIL") {
        doLogout(dispatch, history); // Do logout
    } else {
        dispatch(successAction);
    }
}

function doLogout(dispatch, history) {
    localStorage.removeItem('jwtToken');
    history.replace('/');
    dispatch(receiveLogout());
}