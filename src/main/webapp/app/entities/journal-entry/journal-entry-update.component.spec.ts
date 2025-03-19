import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import JournalEntryUpdate from './journal-entry-update.vue';
import JournalEntryService from './journal-entry.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type JournalEntryUpdateComponentType = InstanceType<typeof JournalEntryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const journalEntrySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<JournalEntryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('JournalEntry Management Update Component', () => {
    let comp: JournalEntryUpdateComponentType;
    let journalEntryServiceStub: SinonStubbedInstance<JournalEntryService>;

    beforeEach(() => {
      route = {};
      journalEntryServiceStub = sinon.createStubInstance<JournalEntryService>(JournalEntryService);
      journalEntryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          journalEntryService: () => journalEntryServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(JournalEntryUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(JournalEntryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.journalEntry = journalEntrySample;
        journalEntryServiceStub.update.resolves(journalEntrySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(journalEntryServiceStub.update.calledWith(journalEntrySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        journalEntryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(JournalEntryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.journalEntry = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(journalEntryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        journalEntryServiceStub.find.resolves(journalEntrySample);
        journalEntryServiceStub.retrieve.resolves([journalEntrySample]);

        // WHEN
        route = {
          params: {
            journalEntryId: `${journalEntrySample.id}`,
          },
        };
        const wrapper = shallowMount(JournalEntryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.journalEntry).toMatchObject(journalEntrySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        journalEntryServiceStub.find.resolves(journalEntrySample);
        const wrapper = shallowMount(JournalEntryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
