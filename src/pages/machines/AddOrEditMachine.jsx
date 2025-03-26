import useSWR from 'swr';
import MachineForm from '../../components/machines/MachineForm';
import AsyncData from '../../components/AsyncData';
import useSWRMutation from 'swr/mutation';
import { useParams } from 'react-router-dom';
import { getAll, save, getById } from '../../api';

export default function AddOrEditMachine() {
  const { id } = useParams();

  const { trigger: saveMachine, error: saveError } = useSWRMutation(
    'machines',
    save,
  );

  const {
    data: machine,
    error: machineError,
    isLoading: machineLoading,
  } = useSWR(id ? `machines/${id}` : null, getById);

  const {
    data: sites = [],
    error: sitesError,
    isLoading: sitesLoading,
  } = useSWR('/sites', getAll);

  const {
    data: users = [],
    error: usersError,
    isLoading: usersLoading,
  } = useSWR('/users', getAll);  

  const technicians = users.filter((user) => user.rol === 'TECHNIEKER');
  console.log(technicians);
  
  return (
    <>
      <h1 data-cy="add-edit-machine-title">{ id ? 'Edit machine' : 'Add machine'}</h1>

      <AsyncData 
        error={saveError || sitesError || machineError || usersError} 
        loading={sitesLoading || machineLoading || usersLoading}
      >
        <MachineForm 
          sites={sites}
          machine={machine} 
          saveMachine={saveMachine}
          technicians={technicians}
          data-cy="machine-form"
        />
      </AsyncData>
    </>
  );
}
