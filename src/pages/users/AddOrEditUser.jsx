import { useParams } from 'react-router';
import useSWR from 'swr';
import { getById, save } from '../../api/index';
import useSWRMutation from 'swr/mutation';
import AsyncData from '../../components/AsyncData';
import UserForm from '../../components/users/UserForm';

export default function AddOrEditUser() {
  const { id } = useParams();
  const {
    data: user,
    error: userError,
    isLoading: userLoading,
  } = useSWR(id ? `users/${id}` : null, getById);

  const { trigger: saveUser, error: saveError} = useSWRMutation(id ? 'users' : 'auth/register', save);

  return (
    <div>
      <h1 data-cy="add-edit-user-title">{id ? 'Edit' : 'Add' } User</h1>
      <AsyncData loading={userLoading} error={userError || saveError}>
        <UserForm saveUser={saveUser} user={user}/>
      </AsyncData>
    </div>
  );
}