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
  } = useSWR('sites', getAll);

  return (
    <>
      <h1>{ id ? 'Edit machine' : 'Add machine'}</h1>

      <AsyncData 
        error={saveError || sitesError || machineError} 
        loading={sitesLoading || machineLoading}
      >
        <MachineForm 
          sites={sites}
          machine={machine} 
          saveMachine={saveMachine}
        />
      </AsyncData>
    </>
  );
}
