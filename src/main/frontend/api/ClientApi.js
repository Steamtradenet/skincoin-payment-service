/****************************************************************/
/* BACKEND                                                      */
/****************************************************************/

export const loginUser = (creds) => {

    let request = {
        method: 'POST',
        headers: { 'Content-Type':'application/x-www-form-urlencoded' },
        body: `username=${creds.username}&password=${creds.password}`
    };

    return fetch('/login', request).then(function(response) {
        return response.json();
    });
};


export const checkAuth = () => {

    let request = {
        method: 'GET',
        headers: { 'authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    };

    return fetch('/admin/me', request).then(function(response) {
        return response.json();
    });
};


export const getApps = () => {

    let request = {
        method: 'GET',
        headers: { 'authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    };

    return fetch('/admin/app', request).then(function(response) {
        return response.json();
    });
};


export const saveApp = (app) => {

    let request = {
        method: 'POST',
        headers: {
            'Content-type': 'application/json',
            'authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify(app)
    };

    return fetch('/admin/app', request).then(function(response) {
        return response.json();
    });
};



export const getEthereumInfo = () => {

    let request = {
        method: 'GET',
        headers: { 'authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    };

    return fetch('/api/ethereum/getInfo', request).then(function(response) {
        return response.json();
    });

};



/****************************************************************/
/* MOCKS                                                        */
/****************************************************************/

//export const loginUser = (creds) => {
//
//    const response = {
//        token: '1a2b3c4d'
//    };
//    return new Promise(resolve => setTimeout(resolve(response), 1000));
//};
//
//
//export const checkAuth = () => {
//
//    const response = {
//        id: 1,
//        name: "Alex",
//        role: 'ROLE_ADMIN'
//    };
//    return new Promise(resolve => setTimeout(resolve(response), 1000));
//};
//
//
//export const getApps = () => {
//
//    const response = [{
//        id: 1,
//        name: "Test App",
//        token: '1234',
//        from_address: '0x6e71346880e357a83ae24edd499f4a3fa2cb1825',
//        from_password: 'w4Fh_78zsdf@',
//        callback_url: 'http://myservice/paymentCallback',
//        enable_callback: true
//    }];
//    return new Promise(resolve => setTimeout(resolve(response), 1000));
//};
//
//
//export const saveApp = (app) => {
//
//    const response = app;
//    return new Promise(resolve => setTimeout(resolve(response), 1000));
//};
//
//
//
//export const getEthereumInfo = () => {
//
//    const response = {
//        network: "MAIN_NET",
//        "skincoin_address": "0x2bdc0d42996017fce214b21607a515da41a9e0c5",
//        "client_version": "Geth/v1.6.7-stable/linux-amd64/go1.7.3"
//    };
//
//    //const response = {
//    //    network: "PRIVATE_NET",
//    //    "skincoin_address": "0x6e71346880e357a83ae24edd499f4a3fa2cb1825",
//    //    "client_version": "Geth/v1.6.7-stable/linux-amd64/go1.7.3"
//    //};
//    return new Promise(resolve => setTimeout(resolve(response), 1000));
//};