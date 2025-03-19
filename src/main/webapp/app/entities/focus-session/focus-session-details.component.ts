import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import FocusSessionService from './focus-session.service';
import { useDateFormat } from '@/shared/composables';
import { type IFocusSession } from '@/shared/model/focus-session.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FocusSessionDetails',
  setup() {
    const dateFormat = useDateFormat();
    const focusSessionService = inject('focusSessionService', () => new FocusSessionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const focusSession: Ref<IFocusSession> = ref({});

    const retrieveFocusSession = async focusSessionId => {
      try {
        const res = await focusSessionService().find(focusSessionId);
        focusSession.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.focusSessionId) {
      retrieveFocusSession(route.params.focusSessionId);
    }

    return {
      ...dateFormat,
      alertService,
      focusSession,

      previousState,
    };
  },
});
