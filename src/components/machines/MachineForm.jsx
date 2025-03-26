import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { SITE_DATA } from '../../api/mock_data';

const validationRules = {
  site_id: {
    required: 'Site ID is required',
  },
  locatie: {
    required: 'Location is required',
  },
  info: {
    required: 'Info is required',
  },
};

const EMPTY_MACHINE = {
  id: undefined,
  site_id: '',
  locatie: '',
  info: '',
};

export default function MachineForm({ sites = [], machine = EMPTY_MACHINE, saveMachine }) {
  sites = SITE_DATA; // MOCK DATA!!, wegdoen als werkelijke data beschikbaar is

  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      site_id: machine?.site_id,
      locatie: machine?.locatie,
      info: machine?.info,
    },
  });

  const onSubmit = async (values) => {
    if (!isValid) return;

    await saveMachine({
      id: machine?.id,
      ...values,
    }, {
      throwOnError: false,
      onSuccess: () => navigate('/machines'),
    });
  };

  return (
    <div className='form-container'>
      <form onSubmit={handleSubmit(onSubmit)} className='machine-form'>
        <div className='mb-3'>
          <label htmlFor='site_id' className='form-label'>
            Site ID
          </label>
          <select
            {...register('site_id', validationRules.site_id)}
            id='site_id'
            name='site_id'
            className='form-select'
          >
            <option value='' disabled>
              -- Select a site --
            </option>
            {sites.map(({ id, naam }) => (
              <option key={id} value={id}>
                {naam}
              </option>
            ))}
          </select>
          {errors.site_id && <p className="form-text text-danger">{errors.site_id.message}</p>}
        </div>

        <div className='mb-3'>
          <label htmlFor='locatie' className='form-label'>
            Location
          </label>
          <input
            {...register('locatie', validationRules.locatie)}
            id='locatie'
            name='locatie'
            type='text'
            className='form-control'
          />
          {errors.locatie && <p className="form-text text-danger">{errors.locatie.message}</p>}
        </div>

        <div className='mb-3'>
          <label htmlFor='info' className='form-label'>
            Info
          </label>
          <textarea
            {...register('info', validationRules.info)}
            id='info'
            name='info'
            className='form-control'
          />
          {errors.info && <p className="form-text text-danger">{errors.info.message}</p>}
        </div>

        <div className='clearfix'>
          <div className='btn-group float-end'>
            <button type='submit' className='btn btn-primary'>
              {machine?.id ? 'Save Machine' : 'Add Machine'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}