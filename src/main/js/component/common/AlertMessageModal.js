import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'react-bootstrap';
import TextInput from '../../field/input/TextInput';

class AlertMessageModal extends Component {
    render() {
        return (<Modal show={this.props.showMessageModal} onHide={this.props.handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>{this.props.modalTitle}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <TextInput id="message" label="" readOnly name="message" value={this.props.message} />
            </Modal.Body>
            <Modal.Footer>
                <button id="testCancel" type="button" className="btn btn-link" on Click={this.props.handleClose}>Cancel</button>
            </Modal.Footer>
        </Modal>);
    }
}

AlertMessageModal.propTypes = {
    handleClose: PropTypes.func,
    showMessageModal: PropTypes.bool.isRequired,
    modalTitle: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired
};

AlertMessageModal.defaultProps = {
    handleClose: () => {
        this.setProperty({ showMessageModal: false });
    }
};

export default AlertMessageModal;
