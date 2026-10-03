import useSWR from 'swr';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAll } from '../../api';
import AsyncData from '../../components/AsyncData';

export default function UsersList() {
  const { data: users = [], loading: usersLoading, error: usersError } = useSWR('/users', getAll);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const navigate = useNavigate();

  const roles = [...new Set(users.map((user) => user.rol))];

  const filteredUsers = users.filter((user) =>
    (user.id.toString().includes(searchTerm) ||
    `${user.voornaam} ${user.achternaam}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())) &&
    (selectedRole === '' || user.rol === selectedRole),
  );

  const handleRowClick = (id) => {
    navigate(`/users/${id}`);
  };

  return (
    <div>
      <div className='input-group'>
        <input
          className='user-search-bar'
          type="text"
          placeholder="Search by ID, name, or email"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          data-cy="user-search-bar"
        />
        <select
          className='states'
          value={selectedRole}
          onChange={(e) => setSelectedRole(e.target.value)}
          data-cy="role-select"
        >
          <option value=''>All Roles</option>
          {roles.map((role) => (
            <option key={role} value={role}>{role}</option>
          ))}
        </select>
        <div>
          <button className='btn btn-primary' onClick={() => navigate('/users/add')} 
            data-cy="add-user-btn">Add User</button>
        </div>
      </div>
      <AsyncData loading={usersLoading} error={usersError}>
        <div className="">
          <table className='user-table'>
            <thead>
              <tr>
                <th>ID </th>
                <th>Name </th>
                <th>Email </th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((user) => (
                <tr
                  key={user.id}
                  style={{ color: user.actief ? 'inherit' : '#b6b8b8', cursor: 'pointer' }}
                  onClick={() => handleRowClick(user.id)}
                  data-cy="user-row"
                >
                  <td>{user.id}</td>
                  <td>{user.voornaam + ' ' + user.achternaam}</td>
                  <td>{user.email}</td>
                  <td>{user.rol}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </AsyncData>
    </div>
  );
}