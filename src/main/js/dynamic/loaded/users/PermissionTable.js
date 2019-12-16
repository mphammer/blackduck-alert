import React, { Component } from 'react';
import PropTypes from 'prop-types';
import TableDisplay from 'field/TableDisplay';
import DynamicSelectInput from 'field/input/DynamicSelect';
import CheckboxInput from 'field/input/CheckboxInput';
import { CONTEXT_TYPE } from 'util/descriptorUtilities';

export const PERMISSIONS_TABLE = {
    DESCRIPTOR_NAME: "descriptorName",
    CONTEXT: "context",
    CREATE: "create",
    DELETE_OPERATION: "delete",
    READ: "read",
    WRITE: "write",
    EXECUTE: "execute",
    UPLOAD_READ: "uploadRead",
    UPLOAD_WRITE: "uploadWrite",
    UPLOAD_DELETE: "uploadDelete"
}


class PermissionTable extends Component {
    constructor(props) {
        super(props);

        this.handlePermissionsChange = this.handlePermissionsChange.bind(this);
        this.createPermissionsModal = this.createPermissionsModal.bind(this);
        this.retrievePermissionsData = this.retrievePermissionsData.bind(this);
        this.createDescriptorOptions = this.createDescriptorOptions.bind(this);
        this.onSavePermissions = this.onSavePermissions.bind(this);
        this.onDeletePermissions = this.onDeletePermissions.bind(this);
        this.onPermissionsClose = this.onPermissionsClose.bind(this);
        this.onUpdatePermissions = this.onUpdatePermissions.bind(this);

        this.state = {
            permissionsData: {}
        };
    }

    handlePermissionsChange(e) {
        const { name, value, type, checked } = e.target;
        const { permissionsData } = this.state;
        const updatedValue = type === 'checkbox' ? checked.toString().toLowerCase() === 'true' : value;
        const trimmedValue = (Array.isArray(updatedValue) && updatedValue.length > 0) ? updatedValue[0] : updatedValue;
        const newPermissions = Object.assign(permissionsData, { [name]: trimmedValue });
        this.setState({
            permissionsData: newPermissions
        });
    }

    createPermissionsColumns() {
        return [
            {
                header: PERMISSIONS_TABLE.DESCRIPTOR_NAME,
                headerLabel: 'Descriptor',
                isKey: true
            }, {
                header: PERMISSIONS_TABLE.CONTEXT,
                headerLabel: 'Context',
                isKey: false
            }, {
                header: 'permissionsColumn',
                headerLabel: 'Permissions',
                isKey: false
            }
        ];
    }

    retrievePermissionsData() {
        const { data } = this.props;
        if (!data) {
            return [];
        }

        const descriptorOptions = this.createDescriptorOptions();

        return data.map(permission => {
            const permissionShorthand = [];
            permission[PERMISSIONS_TABLE.CREATE] && permissionShorthand.push('c');
            permission[PERMISSIONS_TABLE.DELETE_OPERATION] && permissionShorthand.push('d');
            permission[PERMISSIONS_TABLE.READ] && permissionShorthand.push('r');
            permission[PERMISSIONS_TABLE.WRITE] && permissionShorthand.push('w');
            permission[PERMISSIONS_TABLE.EXECUTE] && permissionShorthand.push('x');
            permission[PERMISSIONS_TABLE.UPLOAD_READ] && permissionShorthand.push('ur');
            permission[PERMISSIONS_TABLE.UPLOAD_WRITE] && permissionShorthand.push('uw');
            permission[PERMISSIONS_TABLE.UPLOAD_DELETE] && permissionShorthand.push('ud');

            const descriptorName = permission[PERMISSIONS_TABLE.DESCRIPTOR_NAME];
            const prettyNameObject = descriptorOptions.find(option => descriptorName === option.value);
            const prettyName = (prettyNameObject) ? prettyNameObject.label : descriptorName;

            return {
                [PERMISSIONS_TABLE.DESCRIPTOR_NAME]: prettyName,
                [PERMISSIONS_TABLE.CONTEXT]: permission[PERMISSIONS_TABLE.CONTEXT],
                permissionsColumn: permissionShorthand.join('-')
            };
        });
    }

    convertPermissionsColumn(permissions) {
        const { permissionsColumn, descriptorName, context } = permissions;
        const splitPermissions = permissionsColumn.split('-');

        const prettyNameObject = this.createDescriptorOptions().find(option => descriptorName === option.label);
        const prettyName = (prettyNameObject) ? prettyNameObject.value : descriptorName;

        return {
            descriptorName: prettyName,
            context,
            [PERMISSIONS_TABLE.CREATE]: splitPermissions.includes('c'),
            [PERMISSIONS_TABLE.DELETE_OPERATION]: splitPermissions.includes('d'),
            [PERMISSIONS_TABLE.READ]: splitPermissions.includes('r'),
            [PERMISSIONS_TABLE.WRITE]: splitPermissions.includes('w'),
            [PERMISSIONS_TABLE.EXECUTE]: splitPermissions.includes('x'),
            [PERMISSIONS_TABLE.UPLOAD_READ]: splitPermissions.includes('ur'),
            [PERMISSIONS_TABLE.UPLOAD_WRITE]: splitPermissions.includes('uw'),
            [PERMISSIONS_TABLE.UPLOAD_DELETE]: splitPermissions.includes('ud')
        };
    }

    createDescriptorOptions() {
        const { descriptors } = this.props;
        const descriptorOptions = [];
        const nameCache = [];


        descriptors.forEach(descriptor => {
            const { label, name } = descriptor;
            if (!nameCache.includes(name)) {
                nameCache.push(name);
                descriptorOptions.push({
                    label: label,
                    value: name
                });
            }
        });

        return descriptorOptions;
    }

    createContextOptions() {
        return [{
            label: CONTEXT_TYPE.DISTRIBUTION,
            value: CONTEXT_TYPE.DISTRIBUTION
        }, {
            label: CONTEXT_TYPE.GLOBAL,
            value: CONTEXT_TYPE.GLOBAL
        }]
    }

    onPermissionsClose() {
        this.setState({
            permissionsData: {}
        });
    }

    isMatchingPermissions(first, second) {
        return first[PERMISSIONS_TABLE.DESCRIPTOR_NAME] === second[PERMISSIONS_TABLE.DESCRIPTOR_NAME] &&
            first[PERMISSIONS_TABLE.CONTEXT] === second[PERMISSIONS_TABLE.CONTEXT] &&
            first[PERMISSIONS_TABLE.CREATE] === second[PERMISSIONS_TABLE.CREATE] &&
            first[PERMISSIONS_TABLE.DELETE_OPERATION] === second[PERMISSIONS_TABLE.DELETE_OPERATION] &&
            first[PERMISSIONS_TABLE.READ] === second[PERMISSIONS_TABLE.READ] &&
            first[PERMISSIONS_TABLE.WRITE] === second[PERMISSIONS_TABLE.WRITE] &&
            first[PERMISSIONS_TABLE.EXECUTE] === second[PERMISSIONS_TABLE.EXECUTE] &&
            first[PERMISSIONS_TABLE.UPLOAD_READ] === second[PERMISSIONS_TABLE.UPLOAD_READ] &&
            first[PERMISSIONS_TABLE.UPLOAD_DELETE] === second[PERMISSIONS_TABLE.UPLOAD_DELETE] &&
            first[PERMISSIONS_TABLE.UPLOAD_WRITE] === second[PERMISSIONS_TABLE.UPLOAD_WRITE];
    }

    createPermissionsModal(selectedRow) {
        const { permissionsData } = this.state;
        let newPermissions = permissionsData;
        if (selectedRow) {
            const parsedPermissions = this.convertPermissionsColumn(selectedRow);
            newPermissions = Object.assign({}, parsedPermissions, permissionsData);
            if (!this.isMatchingPermissions(permissionsData, newPermissions)) {
                this.setState({
                    permissionsData: newPermissions
                });
            }
        }

        return (
            <div>
                <DynamicSelectInput name={PERMISSIONS_TABLE.DESCRIPTOR_NAME} id={PERMISSIONS_TABLE.DESCRIPTOR_NAME} label="Descriptor Name" options={this.createDescriptorOptions()} clearable={false} onChange={this.handlePermissionsChange}
                                    value={newPermissions[PERMISSIONS_TABLE.DESCRIPTOR_NAME]} />
                <DynamicSelectInput name={PERMISSIONS_TABLE.CONTEXT} id={PERMISSIONS_TABLE.CONTEXT} label="Context" options={this.createContextOptions()} clearable={false} onChange={this.handlePermissionsChange}
                                    value={newPermissions[PERMISSIONS_TABLE.CONTEXT]} />
                <CheckboxInput name={PERMISSIONS_TABLE.CREATE} label="Create" description="Allow users to create new items with this permission." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.CREATE]} />
                <CheckboxInput name={PERMISSIONS_TABLE.DELETE_OPERATION} label="Delete" description="Allow users to delete items with this permission." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.DELETE_OPERATION]} />
                <CheckboxInput name={PERMISSIONS_TABLE.READ} label="Read" description="This permission shows or hides content for the user." onChange={this.handlePermissionsChange} isChecked={newPermissions[PERMISSIONS_TABLE.READ]} />
                <CheckboxInput name={PERMISSIONS_TABLE.WRITE} label="Write" description="Allow users to edit items with this permission." onChange={this.handlePermissionsChange} isChecked={newPermissions[PERMISSIONS_TABLE.WRITE]} />
                <CheckboxInput name={PERMISSIONS_TABLE.EXECUTE} label="Execute" description="Allow users to perform functionality with this permission." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.EXECUTE]} />
                <CheckboxInput name={PERMISSIONS_TABLE.UPLOAD_READ} label="Upload Read" description="This permission shows or hides upload related content for the user." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.UPLOAD_READ]} />
                <CheckboxInput name={PERMISSIONS_TABLE.UPLOAD_WRITE} label="Upload Write" description="Allow users to modify uploaded content with this permission." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.UPLOAD_WRITE]} />
                <CheckboxInput name={PERMISSIONS_TABLE.UPLOAD_DELETE} label="Upload Delete" description="Allow users to delete uploaded content with this permission." onChange={this.handlePermissionsChange}
                               isChecked={newPermissions[PERMISSIONS_TABLE.UPLOAD_DELETE]} />
            </div>
        );
    }

    onSavePermissions() {
        const { permissionsData } = this.state;
        if (!permissionsData[PERMISSIONS_TABLE.DESCRIPTOR_NAME] || !permissionsData[PERMISSIONS_TABLE.CONTEXT]) {
            console.log('ERROR: Did not select Descriptor name and context');
        } else {
            const { permissionsData } = this.state;
            this.props.saveRole(permissionsData);
            this.setState({
                permissionsData: {}
            });
        }
    }

    onUpdatePermissions() {
        const { permissionsData } = this.state;
        this.props.updateRole(permissionsData);
        this.setState({
            permissionsData: {}
        });
    }

    onDeletePermissions(permissionsToDelete) {
        if (permissionsToDelete) {
            const { permissions } = this.state.role;
            permissions.filter(permission => !permissionsToDelete.includes(permission[PERMISSIONS_TABLE.DESCRIPTOR_NAME])).forEach(permission => {
                this.props.deleteRole(permission);
            });
        }
    }

    render() {
        const { canCreate, canDelete } = this.props;

        return (
            <div>
                <TableDisplay
                    modalTitle="Role Permissions"
                    tableNewButtonLabel="Add"
                    tableDeleteButtonLabel="Remove"
                    tableSearchable={false}
                    autoRefresh={false}
                    tableRefresh={false}
                    onConfigSave={this.onSavePermissions}
                    onConfigUpdate={this.onUpdatePermissions}
                    onConfigDelete={this.onDeletePermissions}
                    onConfigClose={this.onPermissionsClose}
                    newConfigFields={this.createPermissionsModal}
                    columns={this.createPermissionsColumns()}
                    data={this.retrievePermissionsData()}
                    refreshData={() => null}
                    deleteButton={canDelete}
                    newButton={canCreate}
                    sortName={PERMISSIONS_TABLE.DESCRIPTOR_NAME} />
            </div>
        );
    }
}

PermissionTable.propTypes = {
    data: PropTypes.array.isRequired,
    updateRole: PropTypes.func.isRequired,
    saveRole: PropTypes.func.isRequired,
    deleteRole: PropTypes.func.isRequired,
    canCreate: PropTypes.bool,
    canDelete: PropTypes.bool,
    descriptors: PropTypes.array
};

PermissionTable.defaultProps = {
    canCreate: true,
    canDelete: true,
    descriptors: []
};

export default PermissionTable;