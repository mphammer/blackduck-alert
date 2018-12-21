import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Modal} from 'react-bootstrap';
import TextInput from "../../field/input/TextInput";

class AlertMessageModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            message: ''
        };
    }

    render() {
        return (<Modal show={this.props.showMessageModal} onHide={this.props.handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>{this.props.modalTitle}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <TextInput id='message' label='' readOnly={true} name='message' value={this.state.message}/>
            </Modal.Body>
            <Modal.Footer>
                <button id="testCancel" type="button" className="btn btn-link" onClick={this.props.handleClose}>Cancel</button>
            </Modal.Footer>
        </Modal>);
    }
}

AlertMessageModal.propTypes = {
    showMessageModal: PropTypes.bool.isRequired,
    handleClose: PropTypes.func.isRequired,
    modalTitle: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired,
    showPropName: PropTypes.string.isRequired
};

AlertMessageModal.defaultProps = {
    showMessageModal: false,
    handleClose: () => {
        this.setProperty({showMessageModal: false});
    },
};

export default AlertMessageModal;
