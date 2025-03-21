import useSWR from 'swr';
import { getAll } from '../api';
import AsyncData from '../components/AsyncData';
import { FaSort } from 'react-icons/fa';

export default function Users() {
  
  const {data: users = [],
    loading: usersLoading,
    error: usersError} = useSWR('/users', getAll);

  console.log(users);
  
  return (
    <div>
      <h1>Users</h1>
      <AsyncData loading={usersLoading} error={usersError}>
        <div className="machine-tabel-big-container">
          <table className='machine-tabel-big'>
            <thead>
              <tr>
                <th>ID <FaSort /></th>
                <th>Name <FaSort /></th>
                <th>Email <FaSort /></th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </AsyncData>
    </div>
  );
}