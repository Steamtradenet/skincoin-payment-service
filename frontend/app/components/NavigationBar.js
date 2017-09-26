import React, { Component } from 'react';

let Navbar = require("react-bootstrap/lib/Navbar");
let NavItem = require("react-bootstrap/lib/NavItem");
let Nav = require ("react-bootstrap/lib/Nav");
let NavDropdown = require ("react-bootstrap/lib/NavDropdown");
let MenuItem = require ("react-bootstrap/lib/MenuItem");

class NavigationBar extends Component {

    constructor(props, context) {
        super(props, context);

        this.state = {

        };
    }

    render() {

        const appNavbar = (
            <Navbar inverse collapseOnSelect>
                <Navbar.Header>
                    <Navbar.Brand>
                        <a href="#">
                            <img src="images/steamtrade.png" style={{width:130}} />
                        </a>
                    </Navbar.Brand>
                    <Navbar.Toggle />
                </Navbar.Header>
                <Navbar.Collapse>
                    <Nav pullRight>
                        <NavDropdown eventKey={3} title={<span><i className="glyphicon glyphicon-user" style={{paddingRight:5}}></i>  {this.props.userName}</span>}
                                     id="basic-nav-dropdown">
                            <MenuItem onClick={() => this.props.onLogout()} eventKey={3.1}>Logout</MenuItem>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );

        return (
            <div>
                {appNavbar}
            </div>
        );
    }
}

export default NavigationBar;


//<NavDropdown eventKey={3} title="Users" id="basic-nav-dropdown">
//    <MenuItem eventKey={3.1}>Action</MenuItem>
//    <MenuItem eventKey={3.2}>Another action</MenuItem>
//    <MenuItem eventKey={3.3}>Something else here</MenuItem>
//    <MenuItem divider />
//    <MenuItem eventKey={3.4}>Separated link</MenuItem>
//</NavDropdown>
