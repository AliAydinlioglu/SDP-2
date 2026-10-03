import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

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
  technieker_id: {
    required: 'Technician is required',
  },
};

const EMPTY_MACHINE = {
  id: undefined,
  site_id: '',
  locatie: '',
  info: '',
  status: 'inactive',
  prod_status: 'idle',
  uptime: 0,
  technieker_id: null,
  onderhouden: [],
  dagenSindsOnderhoud: 0,
  volgendOnderhoud: new Date(),
};

export default function MachineForm({ sites = [], technicians = [], machine = EMPTY_MACHINE, saveMachine }) {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      site_id: machine?.site_id,
      locatie: machine?.locatie,
      info: machine?.info,
      status: machine?.status,
      prod_status: machine?.prod_status,
      uptime: machine?.uptime,
      technieker_id: machine?.technieker_id,
      // onderhouden: machine?.onderhouden,
      dagenSindsOnderhoud: machine?.dagenSindsOnderhoud,
      volgendOnderhoud: machine?.volgendOnderhoud,
    },
  });

  const onSubmit = async (values) => {
    if (!isValid) return;
    console.log(values);
    
    await saveMachine({
      id: machine?.id,
      ...values,
      site_id: parseInt(values.site_id, 10),
      technieker_id: parseInt(values.technieker_id, 10),
      volgendOnderhoud: new Date(values.volgendOnderhoud).toISOString(),
    }, {
      throwOnError: false,
      onSuccess: () => navigate('/machines'),
    });
  };

  return (
    <div className='form-container'>
      <form onSubmit={handleSubmit(onSubmit)} className='machine-form' data-cy="machine-form">
        <div className='mb-3'>
          <label htmlFor='site_id' className='form-label'>
            Site
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
          <label htmlFor='technieker_id' className='form-label'>
            Technician
          </label>
          <select
            {...register('technieker_id', validationRules.technieker_id)}
            id='technieker_id'
            name='technieker_id'
            className='form-select'
          >
            <option value='' disabled>
              -- Select a technician --
            </option>
            {technicians.map(({ id, voornaam, achternaam }) => (
              <option key={id} value={id}>
                {voornaam} {achternaam}
              </option>
            ))}
          </select>
          {errors.technieker_id && <p className="form-text text-danger">{errors.technieker_id.message}</p>}
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
          <label htmlFor='info' className='form-label' data-cy="info-label">
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