import { useAuth } from '../context/Auth.context';
import useSWRMutation from 'swr/mutation';
import { save } from '../api/index';
import AsyncData from './AsyncData';
import { useForm, FormProvider } from 'react-hook-form';
import { useNavigate, useParams } from 'react-router-dom';
import LabelInput from './LabelInput';

export default function AddOrEditOnderhoud() {
  const { user } = useAuth();
  const { machine_id } = useParams();
  const navigate = useNavigate();
  const methods = useForm({
    defaultValues: {
      datum: new Date().toISOString().split('T')[0],
      technieker_id: user.id,
      machine_id: Number(machine_id),
    },
  });

  const {
    trigger: saveOnderhoud,
    error: saveError,
  } = useSWRMutation('onderhoud', save);

  const onSubmit = async (data) => {
    await saveOnderhoud(data);
    navigate(`/machines/${machine_id}`);
  };

  return (
    <div>
      <h1>Add Maintenance</h1>
      <AsyncData error={saveError}>
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)} className='onderhoud-form'>
            <LabelInput label="Date" name="datum" type="date" validationRules={{ required: 'Date is required' }} />
            <LabelInput label="Start Time" name="startTijd" 
              type="time" validationRules={{ required: 'Start time is required' }} />
            <LabelInput label="End Time" name="eindTijd"
              type="time" validationRules={{ required: 'End time is required' }} />
            <LabelInput label="Reason" name="reden" type="text" validationRules={{ required: 'Reason is required' }} />
            <LabelInput label="Status" name="status" type="text" validationRules={{ required: 'Status is required' }} />
            <LabelInput label="Remarks" name="opmerkingen" 
              type="text" validationRules={{ required: 'Remarks are required' }} />
            <button type="submit" className="btn btn-light">Save</button>
          </form>
        </FormProvider>
      </AsyncData>
    </div>
  );
}