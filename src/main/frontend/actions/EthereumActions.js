import * as clientApi from '../api/ClientApi';

export const GET_INFO_REQUEST = 'GET_INFO_REQUEST';
export const GET_INFO_SUCCESS = 'GET_INFO_SUCCESS';
export const GET_INFO_FAILURE = 'GET_INFO_FAILURE';

function requestInfo() {
    return {
        type: GET_INFO_REQUEST
    }
}

function receiveInfo(response) {
    return {
        type: GET_INFO_SUCCESS,
        payload: response
    }
}

function getInfoError(message) {
    return {
        type: GET_INFO_FAILURE,
        error: message
    }
}


export function getEthereumInfo() {
    return (dispatch) => {

        dispatch(requestInfo());

        return clientApi.getEthereumInfo().then(response => {

            dispatch(receiveInfo(response));

        }).catch(error => {
            console.log(error);
            dispatch(getInfoError(error));
        });
    };
}