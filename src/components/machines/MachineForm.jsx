import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { SITE_DATA } from '../../api/mock_data';

const validationRules = {
  site_id: {
    required: 'site id is required',
  },
};

const EMPTY_MACHINE = {
  id: undefined,
  site_id: '',
  state: '',
  prod_state: '',
};

export default function MachineForm({ sites = [], machine=EMPTY_MACHINE, saveMachine }) {
  sites = SITE_DATA; //MOCK DATA!!, wegdoen als werkelijke data beschikbaar is

  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      site_id: machine?.site_id,
      state: machine?.state,
      prod_state: machine?.prod_state,
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
            {sites.map(({ id, name }) => (
              <option key={id} value={id}>
                {name}
              </option>
            ))}
          </select>
          {errors.site_id ? <p className="form-text text-danger">{errors.site_id.message}</p> : null}
        </div>

        <div className='mb-3'>
          <label htmlFor='state' className='form-label'>
            State
          </label>
          <select
            {...register('state')}
            id='state'
            name='state'
            className='form-select'
          >
            <option value='' disabled>
              -- Select a state --
            </option>
            <option value='active'>Active</option>
            <option value='inactive'>Inactive</option>
          </select>
        </div>

        <div className='mb-3'>
          <label htmlFor='prod_state' className='form-label'>
            Production State
          </label>
          <select
            {...register('prod_state')}
            id='prod_state'
            name='prod_state'
            className='form-select'
          >
            <option value='' disabled>
              -- Select a production state --
            </option>
            <option value='running'>Running</option>
            <option value='maintenance'>Maintenance</option>
            <option value='idle'>Idle</option>
          </select>
        </div>

        <div className='clearfix'>
          <div className='btn-group float-end'>
            <button type='submit' className='btn btn-primary'>
              {machine?.id
                ? 'Save machine'
                : 'Add machine'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
