import * as clientApi from '../api/ClientApi';

export const GET_APPS_REQUEST = 'GET_APPS_REQUEST';
export const GET_APPS_SUCCESS = 'GET_APPS_SUCCESS';
export const GET_APPS_FAILURE = 'GET_APPS_FAILURE';

export const ADD_APP_REQUEST = 'ADD_APP_REQUEST';
export const ADD_APP_SUCCESS = 'ADD_APP_SUCCESS';
export const ADD_APP_FAILURE = 'ADD_APP_FAILURE';

export const UPDATE_APP_REQUEST = 'UPDATE_APP_REQUEST';
export const UPDATE_APP_SUCCESS = 'UPDATE_APP_SUCCESS';
export const UPDATE_APP_FAILURE = 'UPDATE_APP_FAILURE';


function requestGetApps() {
    return {
        type: GET_APPS_REQUEST
    }
}
function receiveGetApps(data) {
    return {
        type: GET_APPS_SUCCESS,
        payload: data
    }
}
function getAppsError(message) {
    return {
        type: GET_APPS_FAILURE,
        error: message
    }
}


export function getApps() {
    return (dispatch) => {

        dispatch(requestGetApps());

        return clientApi.getApps().then(response => {

            dispatch(receiveGetApps(response));

        }).catch(error => {
            console.log(error);
            dispatch(getAppsError(error));
        });
    };
}


function requestAddApp(app) {
    return {
        type: ADD_APP_REQUEST,
        payload: app
    }
}

function receiveAddApp(id) {
    return {
        type: ADD_APP_SUCCESS,
        payload: id
    }
}

function addAppError(message) {
    return {
        type: ADD_APP_FAILURE,
        error: message
    }
}

export function addApp(app) {
    return (dispatch) => {

        dispatch(requestAddApp());

        return clientApi.saveApp(app).then(response => {

            dispatch(receiveAddApp(response));

        }).catch(error => {
            console.log(error);
            dispatch(addAppError(error));
        });
    };
}

function requestUpdateApp(app) {
    return {
        type: UPDATE_APP_REQUEST,
        payload: app
    }
}

function receiveUpdateApp(id) {
    return {
        type: UPDATE_APP_SUCCESS,
        payload: id
    }
}

function updateAppError(message) {
    return {
        type: UPDATE_APP_FAILURE,
        error: message
    }
}

export function updateApp(app) {
    return (dispatch) => {

        dispatch(requestUpdateApp());

        return clientApi.saveApp(app).then(response => {

            dispatch(receiveUpdateApp(response));

        }).catch(error => {
            console.log(error);
            dispatch(updateAppError(error));
        });
    };
}
