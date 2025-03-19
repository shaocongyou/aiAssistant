import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import SocialConnectionService from './social-connection.service';
import { useDateFormat } from '@/shared/composables';
import { type ISocialConnection } from '@/shared/model/social-connection.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'SocialConnectionDetails',
  setup() {
    const dateFormat = useDateFormat();
    const socialConnectionService = inject('socialConnectionService', () => new SocialConnectionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const socialConnection: Ref<ISocialConnection> = ref({});

    const retrieveSocialConnection = async socialConnectionId => {
      try {
        const res = await socialConnectionService().find(socialConnectionId);
        socialConnection.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.socialConnectionId) {
      retrieveSocialConnection(route.params.socialConnectionId);
    }

    return {
      ...dateFormat,
      alertService,
      socialConnection,

      previousState,
    };
  },
});
