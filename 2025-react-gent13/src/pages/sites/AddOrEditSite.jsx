import useSWR from 'swr';
import SiteForm from '../../components/sites/SiteForm';
import AsyncData from '../../components/AsyncData';
import useSWRMutation from 'swr/mutation';
import { useParams } from 'react-router-dom';
import { save, getById, getAll } from '../../api';

export default function AddOrEditSite() {
  const { id } = useParams();

  const { trigger: saveSite, error: saveError } = useSWRMutation(
    'sites',
    save,
  );

  const {
    data: site,
    error: siteError,
    isLoading: siteLoading,
  } = useSWR(id ? `sites/${id}` : null, getById);

  const {
    data: users = [],
    error: usersError,
    isLoading: usersLoading,
  } = useSWR('/users', getAll);

  // const managers = users.filter((user) => user.rol === 'MANAGER');

  return (
    <>
      <h1>{id ? 'Edit site' : 'Add site'}</h1>

      <AsyncData
        error={saveError || siteError || usersError}
        loading={siteLoading || usersLoading}
      >
        <SiteForm
          site={site}
          saveSite={saveSite}
          managers={users}
        />
      </AsyncData>
    </>
  );
}