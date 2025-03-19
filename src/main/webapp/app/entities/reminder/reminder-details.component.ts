import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ReminderService from './reminder.service';
import { useDateFormat } from '@/shared/composables';
import { type IReminder } from '@/shared/model/reminder.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReminderDetails',
  setup() {
    const dateFormat = useDateFormat();
    const reminderService = inject('reminderService', () => new ReminderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const reminder: Ref<IReminder> = ref({});

    const retrieveReminder = async reminderId => {
      try {
        const res = await reminderService().find(reminderId);
        reminder.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.reminderId) {
      retrieveReminder(route.params.reminderId);
    }

    return {
      ...dateFormat,
      alertService,
      reminder,

      previousState,
    };
  },
});
